#!/usr/bin/env python

#!/usr/bin/env python3

###########################################################################
################################# IMPORTS #################################
###########################################################################

import argparse

import os


import pathlib
import pprint
import sys



import traceback
from typing import Dict, List, Tuple, Set

from neo4j import Driver, GraphDatabase, Result, Session, SummaryCounters
from neo4j.exceptions import ServiceUnavailable

###########################################################################
################################ FUNCTIONS ################################
###########################################################################

def read_args() -> argparse.Namespace:

    # The class argparse.RawTextHelpFormatter is used to keep new lines in help text.
    parser: argparse.ArgumentParser = argparse.ArgumentParser(description="Project HATE Reddit network extractor.", formatter_class=argparse.RawTextHelpFormatter)
    parser.add_argument("-i", "--input", help="path to single file or directory of files.", required=True, type=str)
    parser.add_argument("-o", "--output-dir", help="file information output directory - will be created if it does not exist. If empty, defaults to directory of this Python script.", type=str, default="")

    parser.add_argument("--neo4j-uri", help="Neo4j server URI.", required=False, type=str, default="localhost")
    parser.add_argument("--neo4j-user", help="Neo4j user to access.", required=False, type=str, default="neo4j")
    parser.add_argument("--neo4j-pass", help="Neo4j user password.", required=False, type=str, default="password")

    parser.add_argument("--neo4j-encrypted", help="Neo4j with encrypted database communication.", required=False, action="store_true")

    #neo4j_encrypted

    args: argparse.Namespace = parser.parse_args()

    if args.input is not None:
        # Sanitize and check single input file argument.
        assert(len(args.input) > 0), "> '-input-file' must be a non-empty string. Exiting"
        assert(os.path.exists(args.input) and (os.path.isfile(args.input) or os.path.isdir(args.input))), f"[ERROR] - '-input-file' must be a path to an existing file or directory. Exiting"

    if args.input.startswith('~'):
        args.input = os.path.expanduser(args.input).replace('\\', '/')

    
    # Default output directory to the location of the current script if it is not provided.
    if len(args.output_dir) == 0:
        args.output_dir = os.path.dirname(__file__)

    # Check and create the output directory if it doesn't exist.
    if args.output_dir.startswith('~'):
        args.output_dir = os.path.expanduser(args.output_dir).replace('\\', '/')
    pathlib.Path(args.output_dir).mkdir(parents=True, exist_ok=True)

    assert(os.path.exists(args.output_dir) and os.path.isdir(args.output_dir)), f"[ERROR] - '-output-dir' failed to create output directory. Exiting."

    return args

def __neo4j_create_twitter_user_nodes(self, driver: Driver, account_data: List[Dict]) -> SummaryCounters:
    with driver.session() as session:
        comment_field_names: List[str] = list(account_data[0].keys())
        cypher_single_properties: List[str] = []
        for c in comment_field_names:
            if (not c == "account_id") and (not c == "comment_id"):
                if c == "created_utc":
                    cypher_single_properties.append("tu.{0} = datetime({{ epochSeconds: {1}.{0} }})".format(c, "account"))
                else:
                    cypher_single_properties.append("tu.{0} = {1}.{0}".format(c, "account"))
        cypher_property_string: str = ", ".join(cypher_single_properties)
        
        # https://neo4j.com/docs/cypher-manual/current/clauses/create/#use-parameters-with-create
        cypher_statement = ("UNWIND $account_data AS account "

                    #"MERGE (tu:TwitterUser {user_name:account.name, account_id:account.account_id}) "
                    #"MERGE (tu:TwitterUser {account_id:account.account_id}) "
                    "MERGE (tu:TwitterUser {hate_id:account.account_id}) "
                    "ON CREATE SET PY_REPLACE "
                    "ON MATCH SET PY_REPLACE "

                    "WITH tu AS tu "
                    "CALL apoc.create.addLabels(tu, [\"User\"]) YIELD node "

                    "RETURN distinct 'done' ")

        cypher_params = {"account_data": account_data}

        # Insert the individual properties we recorded earlier.
        cypher_statement = cypher_statement.replace("PY_REPLACE", cypher_property_string)


        try:
            query_result: Result = session.run(cypher_statement, cypher_params)
            return query_result.consume().counters
        except UnicodeEncodeError as e:
            pprint.pprint(e)
            sys.exit(1) 

def read_single_profile_file(file_path: str, locus_map: Dict) -> None:
    with open(file_path, 'r') as fh:
        for l in fh:
            clean: str = l.strip()
            if clean.startswith('>'):

                locus_full_string: str = clean[1:]
                locus_nums: List[str] = locus_full_string[len('locus'):].split('_')
                locus_id: int = int(locus_nums[0])
                locus_seq_id: int = int(locus_nums[1])
            else:
                # Single profile logic.
                # >locus5_1
                # TCGAGGAACCGCTCGAGAGGTGATCCTGTCG
                # >locus5_14
                # ACGAGGAACCGCTCGAGAGGTGATCCTGTCG
                # >locus5_17
                if not locus_id in locus_map:
                    locus_map[locus_id] = []
                locus_map[locus_id].append({
                    'locus_seq_id': locus_seq_id,
                    'sequence': clean
                    })

def read_profiles_matrix(file_path: str, profile_map: Dict) -> None:
    with open(file_path, 'r') as fh:

        lines: List[str] = fh.readlines()

        # Header line 1: 
        # ST	locus1	locus2	locus3	locus4	locus5	locus6	locus7
        header: str = lines[0].strip()
        pm_headers: List[str] = header.split()
        locus_id_sec: List[int] = [int(lid[len('locus'):]) for lid in pm_headers[1:]]
        locus_count: int = len(locus_id_sec)

        for l in lines[1:]:
            clean: str = l.strip()

            tkns: List[int] = [int(t) for t in clean.split()]
            
            profile_id: int = int(tkns[0])

            if not profile_id in profile_map:
                profile_map[profile_id] = {}

            locus_ctr: int = 0
            for lid_val in tkns[1:]:

                curr_locus: int = locus_id_sec[locus_ctr]

                if not curr_locus in profile_map[profile_id]:
                    profile_map[profile_id][curr_locus] = lid_val

                locus_ctr += 1

                

###########################################################################
################################## MAIN ###################################
###########################################################################

if __name__ == '__main__':

    # Example program invocation.
    # python3 ingest_data.py . 
    # In this example invocation, the input directory is '.' and the output directory 
    # defaults to this script's directory.
    

    # Parse the arguments.
    try:
        args: argparse.Namespace = read_args()

        print(f'[INFO] - Arguments:')
        print(f"\t'-i/--input' = {args.output_dir}")
        print(f"\t'-o/--output-dir' = {args.output_dir}")
        print(f"\t'--neo4j-uri' = {args.neo4j_uri}")
        print(f"\t'--neo4j-user' = {args.neo4j_user}")
    except AssertionError as e:
        print(str(e))
        sys.exit(1) 

    # Get list of data file paths.
    files: List[str]
    if os.path.isfile(args.input):
        files = [args.input]
    else:
        input_dir: str = args.input
        all_files = [f for f in os.listdir(input_dir) if os.path.isfile(os.path.join(input_dir, f))]
        files = [f for f in all_files if f.endswith('.txt')]

    files.sort()

    print(f'[INFO] - Found files:')
    for f in files:
        print(f'\t{f}')

    # Ingest file data.
    profile_map: Dict = {}
    locus_map: Dict = {}
    #seq_map: Dict = {}

    for f in files:

        current_profile_id: int = -1
        current_profile = None

        # We are assuming profile-specific files are of the form 'profiles_X.txt'.
        single_profile_keyword: str = 'profiles_'
        if single_profile_keyword in f:
            p_sufix: str = f[f.rfind(single_profile_keyword) + len(single_profile_keyword):]
            profile_id: int = int(p_sufix.split('.')[0])
            print(f'\tProfile:\t{profile_id}')
            current_profile_id = profile_id

            if not current_profile_id in profile_map:
                current_profile = {}
                profile_map[current_profile_id] = current_profile
            else:
                current_profile = profile_map[current_profile_id]

            read_single_profile_file(f, locus_map)

            
        else:
            current_profile_id = -1
            current_profile = None

            # Profiles matrix logic.
            read_profiles_matrix(f, profile_map)


        # is_single_profile: bool = False
        # with open(f, 'r') as fh:
        #     for l in fh:
        #         clean: str = l.strip()
        #         if clean.startswith('>'):
        #             is_single_profile = True

        #             locus_full_string: str = clean[1:]
        #             locus_nums: List[str] = locus_full_string[len('locus'):].split('_')
        #             locus_id: int = int(locus_nums[0])
        #             locus_seq_id: int = int(locus_nums[1])
        #         elif is_single_profile:
        #             # Single profile logic.
        #             # >locus5_1
        #             # TCGAGGAACCGCTCGAGAGGTGATCCTGTCG
        #             # >locus5_14
        #             # ACGAGGAACCGCTCGAGAGGTGATCCTGTCG
        #             # >locus5_17
        #             if not locus_id in locus_map:
        #                 locus_map[locus_id] = []
        #             locus_map[locus_id].append({
        #                 'locus_seq_id': locus_seq_id,
        #                 'sequence': clean
        #                 })
                # else:
                #     # Profiles matrix logic.
                #     locus_count: int = -1
                #     if l.startswith('ST'):
                #         pm_headers: List[str] = l.split()
    pprint.pprint(profile_map)
    pprint.pprint(locus_map)

    sys.exit(0)

    # Setup Neo4j connection.    
    try:
        driver: Driver = GraphDatabase.driver(args.neo4j_uri, auth=(args.neo4j_user, args.neo4j_pass), encrypted=args.neo4j_encrypted)

        # Check Neo4j database constraints and indices for different data sources.
    except ServiceUnavailable as e:
        print("[ERROR] - Could not start the driver connection to the Neo4j database.")
        print("\tReason: {}".format(str(e).lower()))
        print("[ERROR] - Exiting.")
        sys.exit(1)
    
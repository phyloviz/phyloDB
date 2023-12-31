#!/bin/bash
# API REQUEST EXAMPLES

set -e

cd "$(dirname "$0")"

log_checkpoint() {
  local message="$1"
  echo
  printf '%*s\n' "${COLUMNS:-$(tput cols)}" '' | tr ' ' -
  echo "$message"
  printf '%*s\n' "${COLUMNS:-$(tput cols)}" '' | tr ' ' -
}

log_checkpoint "Check server status:"
URL="http://localhost:8080/"
response="$(curl --write-out "%{http_code}" --silent --output /dev/null "$URL" || true)"
if [ "$response" -eq 200 ] || [ "$response" -eq 301 ] || [ "$response" -eq 302 ]; then
    echo "ok"
else
  cat << EOF
Error: response ${response} at ${URL}. Please follow these steps:

# From the root directory, run
# > ./configure.sh
#
# and then
# > ./docker/build-docker.sh
# > ./docker/start-docker.sh
EOF
  exit 1
fi


if [ -z "$TOKEN" ]; then cat << EOF
Error: TOKEN is not defined. Please follow these steps:

# Access https://developers.google.com/oauthplayground/ with the account that
# you added as admin in 'scripts/init/init_data.cypher', and click in 'Authorize
# APIs' with the scope 'https://www.googleapis.com/auth/userinfo.email'. Or
# select it in list.
#
# In step 2, click in 'Exchange authorization code for tokens' and copy your
# own access token to the variable TOKEN below.
#
# Then, execute
#
# > export TOKEN="YOUR_ACCESS_TOKEN"
#
# and run this script again.
EOF
  exit 1
else
  echo "ok TOKEN is defined."
fi

../init/make_init_data.cypher.sh

# Let us try several requests:

log_checkpoint "List all users:"
curl -v --location --request GET 'http://localhost:8080/users?provider=google' --header "Authorization: Bearer $TOKEN"

log_checkpoint "List all taxa"
echo -n "Taxa: "
curl --location --request GET 'http://localhost:8080/taxa?provider=google' --header "Authorization: Bearer $TOKEN"
echo

log_checkpoint "Create taxon:"
curl -v --location --request PUT 'http://localhost:8080/taxa/bbacilliformis?provider=google' \
  --header 'Content-Type: application/json' \
  --header "Authorization: Bearer $TOKEN" \
  --data-raw '{
    "id": "bbacilliformis",
    "description": "Example taxon"
  }'

log_checkpoint "List all loci:"
echo -n "Loci: "
curl --location --request GET 'http://localhost:8080/taxa/bbacilliformis/loci?provider=google' \
  --header "Authorization: Bearer $TOKEN"
echo

log_checkpoint "Delete locus (mark deprecated):"
curl -v --location --request DELETE 'http://localhost:8080/taxa/bbacilliformis/loci/ftsZ?provider=google' \
  --header "Authorization: Bearer $TOKEN"

log_checkpoint "Create locus:"
for x in locus1 locus2 locus3 locus4 locus5 locus6 locus7; do
  curl -v --location --request PUT "http://localhost:8080/taxa/bbacilliformis/loci/$x?provider=google" \
    --header 'Content-Type: application/json' \
    --header "Authorization: Bearer $TOKEN" \
    --data-raw "{
      \"id\": \"$x\",
      \"description\": \"Example locus\"
    }";
done

log_checkpoint "Load alleles:"
for x in 1 2 3 4 5 6 7; do
  curl -v --location --request POST "http://localhost:8080/taxa/bbacilliformis/loci/locus${x}/alleles/files?provider=google" \
    --header 'Content-Type: multipart/form-data' \
    --header "Authorization: Bearer $TOKEN" \
    --form "file=@profiles_${x}.txt" ;
done

log_checkpoint "List alleles:"
for x in 1 2 3 4 5 6 7; do
  echo -n "Alleles[locus${x}]: "
  curl --location --request GET "http://localhost:8080/taxa/bbacilliformis/loci/locus${x}/alleles?provider=google" \
    --header "Authorization: Bearer $TOKEN";
  echo ;
done

echo -n "An allele: "
curl --location --request GET "http://localhost:8080/taxa/bbacilliformis/loci/locus1/alleles/1?provider=google" \
  --header "Authorization: Bearer $TOKEN"
echo

log_checkpoint "List all schemas:"
echo -n "Schemas: "
curl --location --request GET 'http://localhost:8080/taxa/bbacilliformis/schemas?provider=google' \
  --header "Authorization: Bearer $TOKEN"
echo
echo -n "Schema: "
curl --location --request GET 'http://localhost:8080/taxa/bbacilliformis/schemas/mlst7?provider=google' \
  --header "Authorization: Bearer $TOKEN"
echo

log_checkpoint "Create schema:"
curl -v --location --request PUT "http://localhost:8080/taxa/bbacilliformis/schemas/mlst7?provider=google" \
  --header 'Content-Type: application/json' \
  --header "Authorization: Bearer $TOKEN" \
  --data-raw '{
    "taxon_id": "bbacilliformis",
	  "id": "mlst7",
   	"type": "mlst",
	  "description": "demo 7 loci schema",
	  "loci": ["locus1", "locus2", "locus3", "locus4", "locus5", "locus6", "locus7"]
  }';

log_checkpoint "List all projects:"
echo -n "Projects: "
curl --location --request GET 'http://localhost:8080/projects?provider=google' --header "Authorization: Bearer $TOKEN"
echo

log_checkpoint "Create project:"
curl -v --location --request POST 'http://localhost:8080/projects?provider=google' \
  --header 'Content-Type: application/json' \
  --header "Authorization: Bearer $TOKEN" \
  --data-raw '{
    "name": "Test",
    "visibility": "private",
    "description": "Test project",
    "users": [{"id": "aplf@tecnico.pt", "provider": "google"}]
  }'

# Set the project id.
PROJECT=$(curl -s --location --request GET 'http://localhost:8080/projects?provider=google' --header "Authorization: Bearer $TOKEN" | python3 -c "import sys, json; print(json.load(sys.stdin)[0]['id'])")
echo "Project: $PROJECT"

log_checkpoint "List all datasets:"
echo -n "Datasets: "
curl --location --request GET "http://localhost:8080/projects/$PROJECT/datasets?provider=google" \
  --header "Authorization: Bearer $TOKEN"
echo

log_checkpoint "Create dataset:"
curl -v --location --request POST "http://localhost:8080/projects/$PROJECT/datasets?provider=google" \
  --header 'Content-Type: application/json' \
  --header "Authorization: Bearer $TOKEN" \
  --data-raw '{
    "taxon_id": "bbacilliformis",
   	"schema_id": "mlst7",
	  "description": "Example dataset"
  }'

# Set dataset id:
DATASET=$(curl -s --location --request GET "http://localhost:8080/projects/$PROJECT/datasets?provider=google" --header "Authorization: Bearer $TOKEN" | python3 -c "import sys, json; print(json.load(sys.stdin)[0]['id'])")
echo "Dataset: $DATASET"

log_checkpoint "Load profiles:"
curl -v --location --request POST "http://localhost:8080/projects/$PROJECT/datasets/$DATASET/profiles/files?provider=google" \
  --header 'Content-Type: multipart/form-data' \
  --header "Authorization: Bearer $TOKEN" \
  --form 'file=@profiles.txt'

log_checkpoint "List profiles:"
echo -n "Profiles: "
curl --location --request GET "http://localhost:8080/projects/$PROJECT/datasets/$DATASET/profiles?provider=google" \
  --header "Authorization: Bearer $TOKEN"
echo
for x in $(seq 10); do
  echo -n "Profile[${x}]: "
  curl --location --request GET "http://localhost:8080/projects/$PROJECT/datasets/$DATASET/profiles/${x}?provider=google" \
    --header "Authorization: Bearer $TOKEN";
  echo;
done

log_checkpoint "Run inference:"
curl -v --location --request POST "http://localhost:8080/projects/$PROJECT/jobs?provider=google" \
  --header 'Content-Type: application/json' \
  --header "Authorization: Bearer $TOKEN" \
  --data-raw "{
    \"analysis\": \"inference\",
    \"algorithm\": \"goeburst\",
    \"parameters\": [\"$DATASET\", 3]
  }"

log_checkpoint "List inferences:"
echo -n "Inferences: "
curl --location --request GET "http://localhost:8080/projects/$PROJECT/datasets/$DATASET/inferences?provider=google" \
  --header "Authorization: Bearer $TOKEN"
echo
INFERENCE=$(curl -s --location --request GET "http://localhost:8080/projects/$PROJECT/datasets/$DATASET/inferences?provider=google" --header "Authorization: Bearer $TOKEN" | python3 -c "import sys, json; print(json.load(sys.stdin)[0]['id'])")
echo -n "Inference: "
curl --location --request GET "http://localhost:8080/projects/$PROJECT/datasets/$DATASET/inferences/$INFERENCE?provider=google" \
  --header "Authorization: Bearer $TOKEN"
echo



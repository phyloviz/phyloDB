package pt.ist.meic.phylodb.authentication.project.model;

import java.util.List;

public class ProjectInputModel {

	private String name;
	private List<String> users;

	public ProjectInputModel(String name, List<String> users) {
		this.name = name;
		this.users = users;
	}

	public String getName() {
		return name;
	}

	public List<String> getUsers() {
		return users;
	}
}

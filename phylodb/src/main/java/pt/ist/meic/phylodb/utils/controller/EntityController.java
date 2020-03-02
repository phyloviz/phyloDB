package pt.ist.meic.phylodb.utils.controller;

import org.springframework.beans.factory.annotation.Value;

public abstract class EntityController {

	@Value("${jsonLimit}")
	protected String jsonLimit;

	@Value("${fileLimit}")
	protected String fileLimit;

}
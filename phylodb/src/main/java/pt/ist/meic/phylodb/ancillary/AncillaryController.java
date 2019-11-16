package pt.ist.meic.phylodb.ancillary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/ancillary")
public class AncillaryController {

	private AncillaryService service;

	public AncillaryController(AncillaryService service) {
		this.service = service;
	}

	@GetMapping(path = "/")
	public ResponseEntity getAncillaries(
			HttpServletRequest req
	) {
		return null;
	}

	@GetMapping(path = "/{ancillaryKey}/values/")
	public ResponseEntity getAncillariesByKey(
			@PathVariable("ancillaryKey") String key,
			HttpServletRequest req
	) {
		return null;
	}

	@GetMapping(path = "/{ancillaryKey}/values/{ancillaryValue}/")
	public ResponseEntity getAncillaryByKey(
			@PathVariable("ancillaryKey") String key,
			@PathVariable("ancillaryValue") String value,
			HttpServletRequest req
	) {
		return null;
	}

	@PostMapping(path = "/taxon/{taxon}/schema/{schema}/")
	public ResponseEntity postAncillary(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema,
			HttpServletRequest req
	) {
		return null;
	}

}

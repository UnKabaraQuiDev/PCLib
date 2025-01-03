package versioning;

import java.nio.file.Path;

import lu.pcy113.pclib.boot.UpdateFetcher;

public class FixedUpdateFetcher implements UpdateFetcher {

	@Override
	public void downloadFile(Path path) {
		// do nothing
	}
	
}

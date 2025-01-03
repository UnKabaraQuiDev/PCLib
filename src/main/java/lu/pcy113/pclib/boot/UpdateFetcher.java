package lu.pcy113.pclib.boot;

import java.nio.file.Path;

public interface UpdateFetcher {

	void downloadFile(Path path);
	
}

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.PCUtils;

public class FileMain {

	@Test
	public void readByteBufferFromFile() throws IOException {
		/*NextTask.withArg((String s) -> new File(s))
				.thenApply(PCUtils::readFile)
				.catch_((e) -> System.out.println(e.getMessage()))
				.thenApply((s) -> PCUtils.byteBufferToHexStringTable(s, 2, 8))
				.thenApply((s) -> PCUtils.leftPadLine(s, "\t"))
				.thenConsume((s) -> System.out.println(s))
		.run("./src/test/java/FileMain.java");*/
		
		File file = new File("./src/test/java/FileMain.java");
		System.out.println("Reading from: " + file.getAbsolutePath() + " exists ? " + file.exists());

		System.out.println("Content --- START");
		System.out.println(PCUtils.leftPadLine(PCUtils.byteBufferToHexStringTable(PCUtils.readFile(file), 2, 8), "\t"));
		System.out.println("Content --- END   ");
	}

}

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.PCUtils;

public class FileMain {

	@Test
	public void readByteBufferFromFile() throws IOException {
		File file = new File("./src/test/java/FileMain.java");
		System.out.println("Reading from: " + file.getAbsolutePath() + " exists ? " + file.exists());

		System.out.println("Content --- START");
		System.out.println(PCUtils.leftPadLine(PCUtils.byteBufferToHexStringTable(PCUtils.readFile(file), 2, 8), "\t"));
		System.out.println("Content --- END   ");
	}

}

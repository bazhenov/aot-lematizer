package me.bazhenov.aot;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

public class MmapDictionary {

	private final MappedByteBuffer prefixBlock;
	private final MappedByteBuffer postfixBlock;
	private final MmapTrie prefixTrie;
	private final MmapTrie postfixTrie;

	public MmapDictionary(File dictFile) throws IOException {
		try (RandomAccessFile f = new RandomAccessFile(dictFile, "r")) {
			long length = f.length();
			f.seek(length - 8);
			int prefixTreeBlockAddress = f.readInt();
			int postfixTreeBlockAddress = f.readInt();

			prefixBlock = f.getChannel().map(READ_ONLY, prefixTreeBlockAddress,
				postfixTreeBlockAddress - prefixTreeBlockAddress);
			postfixBlock = f.getChannel().map(READ_ONLY, postfixTreeBlockAddress,
				length - postfixTreeBlockAddress);
		}

		prefixTrie = new MmapTrie(prefixBlock);
		postfixTrie = new MmapTrie(prefixBlock);
	}

	public void checkExists(String word) {
		
	}
}

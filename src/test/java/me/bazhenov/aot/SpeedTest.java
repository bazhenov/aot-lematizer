package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.io.*;

import static me.bazhenov.aot.Node.readFrom;

public class SpeedTest {

	@Test
	public void testLocation() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(out);
		long start = System.nanoTime();
		for (int i=0; i<100000; i++) {
			new Node().writeTo(os);
		}
		long end = System.nanoTime();
		System.out.println("Written in: " + ((end - start) / 1000000));


		DataInputStream is = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
		start = System.nanoTime();
		for (int i=0; i<100000; i++) {
			readFrom(is);
		}
		end = System.nanoTime();
		System.out.println("Read in: " + ((end - start) / 1000000));

	}
}

final class Node {
	final char a = 'a';
	final int left = 0, right = 0, center = 0;
	final boolean wordEnd = false;


	public void writeTo(DataOutputStream out) throws IOException {
		out.writeChar(a);
		out.writeInt(left);
		out.writeInt(right);
		out.writeInt(center);
		out.writeBoolean(wordEnd);
	}

	public static Node readFrom(DataInputStream in) throws IOException {
		in.readChar();
		in.readInt();
		in.readInt();
		in.readInt();
		in.readBoolean();
		return null;
	}
}
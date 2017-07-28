package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;

import static java.nio.ByteBuffer.allocate;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("Duplicates")
public class MmapIntListTest {

	@Test
	public void testSimpleList() {
		MmapIntList.IntIterator iter = prepare(l -> {
			l.add(3);
			l.add(0x7FFFFFFF);
			l.add(0x7FFFFFFE);
			l.add(0x7000FFFF);
			l.add(1);
			l.add(5);
			l.add(18);
		});

		assertThat(iter.next(), is(1));
		assertThat(iter.next(), is(3));
		assertThat(iter.next(), is(5));
		assertThat(iter.next(), is(18));
		assertThat(iter.next(), is(0x7000FFFF));
		assertThat(iter.next(), is(0x7FFFFFFE));
		assertThat(iter.next(), is(0x7FFFFFFF));
		assertThat(iter.next(), is(0));
	}

	@Test
	public void merge() {
		MmapIntList.IntIterator l1 = prepare(l -> {
			l.add(1);
			l.add(3);
			l.add(35);
			l.add(39);
		});

		MmapIntList.IntIterator l2 = prepare(l -> {
			l.add(1);
			l.add(5);
			l.add(18);
			l.add(39);
		});

		assertThat(l1.nextCommon(l2), is(1));
		assertThat(l1.nextCommon(l2), is(39));
		assertThat(l1.nextCommon(l2), is(0));
	}

	@Test
	public void merge2() {
		MmapIntList.IntIterator l1 = prepare(l -> {
			l.add(1);
			l.add(3);
			l.add(35);
			l.add(39);
		});


		MmapIntList.IntIterator l2 = prepare(l -> {
			l.add(3);
			l.add(5);
			l.add(18);
			l.add(19);
			l.add(35);
		});
		assertThat(l1.nextCommon(l2), is(3));
		assertThat(l1.nextCommon(l2), is(35));
		assertThat(l1.nextCommon(l2), is(0));
	}

	private static MmapIntList.IntIterator prepare(Consumer<Set<Integer>> consumer) {
		SortedSet<Integer> ints = new TreeSet<>();
		consumer.accept(ints);

		ByteBuffer b = allocate(4 + ints.size() * 4);
		MmapIntList.writeToByteBuffer(ints).accept(b);
		b.flip();

		return new MmapIntList(b).iterator().reset(0);
	}
}

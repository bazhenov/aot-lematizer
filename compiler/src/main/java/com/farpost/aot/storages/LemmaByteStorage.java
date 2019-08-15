package com.farpost.aot.storages;


import com.farpost.aot.Flexion;

import java.util.List;

public class LemmaByteStorage extends ByteStorage {

	private final MorphologyTagsByteStorage morph = new MorphologyTagsByteStorage();
	private int morphIndex = 0;
	private final FlexionStringByteStorage flex = new FlexionStringByteStorage();
	private int flexIndex = 0;

	public LemmaByteStorage() {
		super(10208652);
	}

	public void addLemma(List<Flexion> flexions) {
		// тут компилим леммы в байты индексов
		var bytes = new byte[flexions.size() * 2 * 4];
		for (int i = 0, j = 0; i < bytes.length; i += 2, ++j, ++morphIndex, ++flexIndex) {
			var currentFlexion = flexions.get(j);
			morph.addTags(currentFlexion.getTags());
			bytes[i] = morphIndex;
			flex.addString(currentFlexion.getString());

		}
	}

	public byte[] getLemmaLinksBytes() {
		return super.getAllBytes();
	}

	public byte[] getFlexionStringBytes() {
		return flex.getAllBytes();
	}

	public byte[] getMorphBytes() {
		return morph.getAllBytes();
	}
}

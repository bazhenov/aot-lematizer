package com.farpost.aot.storages;


import com.farpost.aot.Flexion;

import java.util.List;

public class LemmaByteStorage extends ByteStorage {

	private final MorphologyTagsByteStorage morph = new MorphologyTagsByteStorage();
	private final FlexionStringByteStorage flex = new FlexionStringByteStorage();

	public LemmaByteStorage() {
		super(10208652);
	}

	public void addLemma(List<Flexion> flexions) {
		b
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

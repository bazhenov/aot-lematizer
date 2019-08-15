package com.farpost.aot.compiler;

import java.io.IOException;

public final class App {

	public static void main(String[] args) throws IOException {
		var allLemmas = LemmasReader.readLemmas();
		var collisionHashes = CollisionDetector.findCollisions(allLemmas);
		for(var lemma: allLemmas) {

		}
	}
}

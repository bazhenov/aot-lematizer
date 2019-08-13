package com.farpost.aot;

import com.farpost.aot.data.Flexion;
import com.farpost.aot.data.MorphologyTag;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class InputData {

	private final Collection<Flexion> collisionFlexions;
	private final Collection<Flexion> normalFlexion;
	private final Collection<String> allLemmas;
	private final Collection<List<MorphologyTag>> grammarInfoVariants;

	private InputData(
		Collection<Flexion> collisionFlexions,
		Collection<Flexion> normalFlexion,
		Collection<String> allLemmas,
		Collection<List<MorphologyTag>> grammarInfoVariants) {

		this.collisionFlexions = collisionFlexions;
		this.normalFlexion = normalFlexion;
		this.allLemmas = allLemmas;
		this.grammarInfoVariants = grammarInfoVariants;
	}


	public Collection<Flexion> getCollisionFlexions() {
		return collisionFlexions;
	}

	public Collection<Flexion> getNormalFlexions() {
		return normalFlexion;
	}

	public Collection<List<MorphologyTag>> getGrammarInfoVariants() {
		return grammarInfoVariants;
	}

	public Collection<String> getAllLemmas() {
		return allLemmas;
	}

	public static InputData prepare() throws IOException {

		System.out.println("Parsing /mrd . . .");
		var store = new FlexionStorage();
		var allFlex = store.getAllFlexion();

		System.out.println("Extracting collisions . . .");
		var removed = CollisionsFilter.extractCollisions(allFlex).getRemovedCollisions();
		System.out.println(String.format("Conflict flexions retrieved: %d", removed.size()));

		return new InputData(removed, allFlex, store.getAllLemmas(), store.getAllGrammarVariants());
	}
}

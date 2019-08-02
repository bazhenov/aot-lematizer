package com.farpost.aot;

import com.farpost.aot.mrd.Flexion;

import java.util.*;
import java.util.stream.Collectors;

public class CollisionsFilter {

	public static class Result {

		public final List<Flexion> removedCollisions;
		public final Set<Integer> collisionHashes;

		private Result(List<Flexion> flex, Set<Integer> hash) {
			removedCollisions = flex;
			collisionHashes = hash;
		}
	}

	private static boolean isCollision(List<Flexion> flexionsWithOneHash) {
		return flexionsWithOneHash.stream()
			.map(x -> x.sourceString)
			.collect(Collectors.toSet())
			.size() > 1;
	}

	public static Result extractCollisions(List<Flexion> flexions) {
		var hashToFlex = new HashMap<Integer, List<Flexion>>();
		for (var i : flexions) {
			hashToFlex
				.computeIfAbsent(i.flexionHash, k -> new ArrayList<>())
				.add(i);
		}

		var collisions = new ArrayList<Flexion>();
		var collisionHashes = new HashSet<Integer>();

		for (var hashAndList : hashToFlex.entrySet()) {
			if (isCollision(hashAndList.getValue())) {

				collisionHashes.add(hashAndList.getKey());
				collisions.addAll(hashAndList.getValue());
			}
		}

		flexions.removeAll(collisions);

		return new Result(collisions, collisionHashes);
	}
}

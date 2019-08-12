package com.farpost.aot;


import com.farpost.aot.data.Flexion;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class CollisionsFilter {

	public static class Result {

		private final Collection<Flexion> removedCollisions;
		private final Collection<Integer> collisionHashes;

		public Collection<Flexion> getRemovedCollisions() {
			return removedCollisions;
		}

		public Collection<Integer> getCollisionHashes() {
			return collisionHashes;
		}

		private Result(Collection<Flexion> flex, Collection<Integer> hash) {
			removedCollisions = flex;
			collisionHashes = hash;
		}
	}

	private static boolean isCollision(Collection<Flexion> flexionsWithOneHash) {
		return flexionsWithOneHash.stream()
			.map(x -> x.source)
			.collect(toSet())
			.size() > 1;
	}

	public static Result extractCollisions(Collection<Flexion> flexions) {
		var hashToFlex = new HashMap<Integer, List<Flexion>>();
		for (var i : flexions) {
			hashToFlex
				.computeIfAbsent(i.source.hashCode(), k -> new ArrayList<>())
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

package com.farpost.aot;


import java.util.*;

import static java.util.stream.Collectors.toSet;

public class CollisionDetector {

	public static class HashCollisions {

		private final Collection<Integer> collisionHashes;

		private HashCollisions(Collection<Integer> hash) {
			collisionHashes = hash;
		}

		public Collection<Integer> getHashes() {
			return collisionHashes;
		}

		public boolean isCollision(int hash) {
			return collisionHashes.contains(hash);
		}
	}

	private static boolean isCollision(Collection<CompilerFlexion> flexionsWithOneHash) {
		return flexionsWithOneHash.stream()
			.map(CompilerFlexion::getString)
			.collect(toSet())
			.size() > 1;
	}

	public static HashCollisions findCollisions(Collection<List<CompilerFlexion>> flexions) {
		var hashToFlex = new HashMap<Integer, List<CompilerFlexion>>();
		for (var i : flexions) {
			for (var j : i) {
				hashToFlex
					.computeIfAbsent(j.getString().hashCode(), k -> new ArrayList<>())
					.add(j);
			}
		}

		var collisionHashes = new HashSet<Integer>();

		for (var hashAndList : hashToFlex.entrySet()) {
			if (isCollision(hashAndList.getValue())) {

				collisionHashes.add(hashAndList.getKey());
			}
		}

		return new HashCollisions(collisionHashes);
	}
}

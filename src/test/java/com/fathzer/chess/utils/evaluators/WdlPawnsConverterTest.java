package com.fathzer.chess.utils.evaluators;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static com.fathzer.chess.utils.evaluators.WdlPawnsConverter.*;

class WdlPawnsConverterTest {

	@Test
	void test() {
		assertEquals(0.5, toWdl(0), 1E-6);
		assertEquals(1, toWdl(3000), 1E-2);
		assertEquals(0, toWdl(-3000), 1E-2);
		
		assertEquals(0, toCentiPawns(0.5));
		assertTrue(toCentiPawns(0.6)>0);
		assertTrue(toCentiPawns(0.4)<0);
		assertThrows(IllegalArgumentException.class, () -> toCentiPawns(1.0));
		assertThrows(IllegalArgumentException.class, () -> toCentiPawns(0.0));
	}

}

// Created by plusminus on 11:32:02 PM - Mar 29, 2009
/**
 * 
 */
package org.andnav2.nav.voice;

import junit.framework.Assert;

import org.andnav2.adt.UnitSystem;
import org.andnav2.adt.voice.AudibleTurnCommand;
import org.andnav2.adt.voice.AudibleTurnCommandManager;
import org.andnav2.adt.voice.SimpleAudibleTurnCommand;
import org.andnav2.adt.voice.TurnVoiceElement;
import org.andnav2.sys.ors.adt.rs.RouteInstruction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Nicolas Gramlich
 * 
 */
public class AudibleTurnCommandTest {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String SAMPLE_DESCRIPTION = "Turn right into Hello-World street.";

	// ===========================================================
	// Fields
	// ===========================================================

	private AudibleTurnCommandManager mAudibleTurnCommandManager;

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.mAudibleTurnCommandManager = new AudibleTurnCommandManager();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	
	@Test
	public void testCreateIfNecessary() throws Exception {
		final RouteInstruction routeInstruction = new RouteInstruction();
		routeInstruction.setAngle(45f);
		routeInstruction.setDescriptionHtml(SAMPLE_DESCRIPTION);
		SimpleAudibleTurnCommand result = mAudibleTurnCommandManager.createIfNeccessary(UnitSystem.METRIC, routeInstruction, 27000, 25000, 50, null);
		
		Assert.assertNotNull(result);
		Assert.assertEquals(TurnVoiceElement.LEFT, result.getTurnVoiceElement());
	}
	
	@Test
	public void testCreateIfNecessaryAndThen() throws Exception {
		final RouteInstruction routeInstruction = new RouteInstruction();
		routeInstruction.setAngle(45f);
		routeInstruction.setLengthMeters(500);
		routeInstruction.setDescriptionHtml(SAMPLE_DESCRIPTION);
		

		final RouteInstruction thenRouteInstruction = new RouteInstruction();
		thenRouteInstruction.setAngle(-45f);
		thenRouteInstruction.setLengthMeters(100);
		thenRouteInstruction.setDescriptionHtml(SAMPLE_DESCRIPTION);
		
		AudibleTurnCommand result = mAudibleTurnCommandManager.createIfNeccessary(UnitSystem.METRIC, routeInstruction, 600, 500, 50, thenRouteInstruction);
		
		Assert.assertNotNull(result);
		Assert.assertEquals(TurnVoiceElement.LEFT, result.getTurnVoiceElement());
		
		Assert.assertTrue(result.hasThenCommand());
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

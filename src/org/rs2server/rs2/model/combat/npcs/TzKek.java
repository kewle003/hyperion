package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;

public class TzKek extends AbstractCombatAction {

	@Override
	public int distance(Mob attacker) {
		return 1;
	}

}

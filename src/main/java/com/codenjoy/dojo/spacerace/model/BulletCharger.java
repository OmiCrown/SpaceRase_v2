package com.codenjoy.dojo.spacerace.model;

import com.codenjoy.dojo.services.Tickable;

public class BulletCharger implements Tickable {
	private final int bulletsCount;
	private int bullets = 0;
	private boolean toRecharge = false;

	public BulletCharger( int bulletsCount) {
		this.bulletsCount = bulletsCount;
	}

	public void setToRecharge(boolean toRecharge) {
		this.toRecharge = toRecharge;
	}

	@Override
	public void tick() {
		if (toRecharge) {
			recharge();
			toRecharge = false;
		}

	}

	private void recharge() {
		bullets = bulletsCount;
	}

	public boolean canShoot() {
		boolean result = bullets > 0;
		if (result) {
			bullets--;
		}
		return result;
	}

	public int getBulletsCount() {
		return bulletsCount;
	}
}

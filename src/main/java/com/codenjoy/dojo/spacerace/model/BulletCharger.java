package com.codenjoy.dojo.spacerace.model;

import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.Tickable;

/**
 * Created by indigo on 08.08.2015.
 */
public class BulletCharger extends PointImpl implements State<Elements, Player>, Tickable{
    private final int ticksToRecharge;
    private final int bulletsCount;
    private int timer = 0;
    private int bullets = 0;

    public BulletCharger(int ticksToRecharge, int bulletsCount) {
        super(2, 2);
        this.ticksToRecharge = ticksToRecharge;
        this.bulletsCount = bulletsCount;
    }

    public BulletCharger(int ticksToRecharge, int bulletsCount, int x, int y) {
        super(x, y);
        this.ticksToRecharge = ticksToRecharge;
        this.bulletsCount = bulletsCount;
    }

    @Override
    public void tick() {
        if (timer == 0) {
            recharge();
        }
        timer--;
    }

    private void recharge() {
        timer = ticksToRecharge;
        bullets = bulletsCount;
    }


    public boolean canShoot() {
        boolean result = bullets > 0;
        if (result) {
            bullets--;
        }
        return result;
    }

    public int getTicksToRecharge() {
        return ticksToRecharge;
    }

    public int getBulletsCount() {
        return bulletsCount;
    }
    @Override
    public Elements state(Player player, Object... alsoAtPoint) {
        return Elements.BULLET_CAHRGER;
    }
}

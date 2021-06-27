/*
 * Copyright (c) 2019, Lucas <https://github.com/lucwousin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.hydraautoprayer.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.api.ProjectileID;

import javax.annotation.Nullable;
import javax.inject.Singleton;

@Getter
@RequiredArgsConstructor
@Singleton
public class Hydra {
    @Nullable
    private final NPC npc;

    private HydraPhase phase = HydraPhase.POISON;

    private AttackStyle nextAttack = AttackStyle.MAGIC;

    private AttackStyle lastAttack = AttackStyle.MAGIC;

    private int nextSwitch = phase.getAttacksPerSwitch();

    public void changePhase(final HydraPhase hydraPhase) {
        phase = hydraPhase;

        if (hydraPhase == HydraPhase.ENRAGED) {
            switchStyles();
            nextSwitch = phase.getAttacksPerSwitch();
        }
    }

    public void handleProjectile(final int projectileId) {
        if (projectileId != nextAttack.getProjectileID()) {
            if (projectileId == lastAttack.getProjectileID()) {
                // If the current attack isn't what was expected and we accidentally counted 1 too much
                return;
            }

            // If the current attack isn't what was expected and we should have switched prayers
            switchStyles();

            nextSwitch = phase.getAttacksPerSwitch() - 1;
        } else {
            nextSwitch--;
        }

        lastAttack = nextAttack;

        if (nextSwitch <= 0) {
            switchStyles();
            nextSwitch = phase.getAttacksPerSwitch();
        }
    }

    private void switchStyles() {
        nextAttack = lastAttack == AttackStyle.MAGIC ? AttackStyle.RANGED : AttackStyle.MAGIC;
    }

    @Getter
    @RequiredArgsConstructor
    public enum AttackStyle {
        MAGIC(ProjectileID.HYDRA_MAGIC, Prayer.PROTECT_FROM_MAGIC),
        RANGED(ProjectileID.HYDRA_RANGED, Prayer.PROTECT_FROM_MISSILES);

        private final int projectileID;
        private final Prayer prayer;
    }
}

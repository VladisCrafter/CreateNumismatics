/*
 * Numismatics
 * Copyright (c) 2023-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.ithundxr.createnumismatics.content.bank.blaze_banker;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class BankAccountBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<BankAccountBehaviour> TYPE = new BehaviourType<>();
    private UUID accountUUID;
    public BankAccountBehaviour(SmartBlockEntity be) {
        super(be);
    }

    public UUID getAccountUUID() {
        if (accountUUID == null) {
            accountUUID = UUID.randomUUID();
            blockEntity.notifyUpdate();
        }
        return accountUUID;
    }

    public BankAccount getAccount() {
        return Numismatics.BANK.getOrCreateAccount(getAccountUUID(), BankAccount.Type.BLAZE_BANKER);
    }

    public boolean hasAccount() {
        if (accountUUID == null)
            return false;
        return Numismatics.BANK.getAccount(accountUUID) != null;
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        if (nbt.hasUUID("accountUUID")) {
            accountUUID = nbt.getUUID("accountUUID");
        }
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        if (accountUUID != null) {
            nbt.putUUID("accountUUID", accountUUID);
        }
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public void destroy() {
        super.destroy();
        BankAccount oldAccount = Numismatics.BANK.accounts.remove(accountUUID);
        if (oldAccount != null)
            oldAccount.setLabel(null);
        Numismatics.BANK.markBankDirty();
    }
}

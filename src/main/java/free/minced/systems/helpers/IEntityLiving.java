package free.minced.systems.helpers;

import free.minced.modules.impl.combat.BackTrack;

import java.util.List;

public interface IEntityLiving {
    double getPrevServerX();

    double getPrevServerY();

    double getPrevServerZ();

    List<BackTrack.Position> getBackTrack();
}

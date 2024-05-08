/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.noah.participantA.facade;

import io.smallrye.mutiny.Uni;
import it.noah.common.ObjectA;
import it.noah.participantA.dao.DaoA;
import it.noah.participantA.entity.EntityA;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;

/**
 *
 * @author NATCRI
 */
@ApplicationScoped
public class FacadeA {

    @Inject
    DaoA dao;

    public Uni<EntityA> createA(String uuid, OffsetDateTime expire) {
        EntityA a = new EntityA();
        a.setTitle("#" + uuid.substring(0, 8) + " A");
        a.setWeight(3);
        a.setEnabled(true);
        a.setCreatetime(OffsetDateTime.now());
        return dao.createA(uuid, expire, a);
    }

    public Uni<EntityA> deleteA(String uuid, OffsetDateTime expire, Long id, Long optlock) {
        return dao.deleteA(uuid, expire, id, optlock);
    }

    public Uni<EntityA> logicallyDeleteA(String uuid, OffsetDateTime expire, Long id, Long optlock) {
        return dao.logicallyDeleteA(uuid, expire, id, optlock);
    }

    public Uni<EntityA> updateA(String uuid, OffsetDateTime expire, Long id, Long optlock, String titlePrefix) {
        return dao.updateA(uuid, expire, id, optlock, titlePrefix);
    }

    public Uni<EntityA> updateArchivingA(String uuid, OffsetDateTime expire, Long id, Long optlock, String titlePrefix) {
        return dao.updateArchivingA(uuid, expire, id, optlock, titlePrefix);
    }

    public Uni<List<ObjectA>> getAllA() {
        return dao.getAllA();
    }

    public Uni<ObjectA> getA(Long id) {
        return dao.getA(id);
    }
}

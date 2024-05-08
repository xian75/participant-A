/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.noah.participantA.dao;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import it.noah.common.ObjectA;
import it.noah.sagacqrs.dao.DaoUtils;
import it.noah.participantA.entity.EntityA;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

/**
 *
 * @author NATCRI
 */
@ApplicationScoped
public class DaoA {

    @Inject
    Logger log;

    @Inject
    PgPool client;

    @Inject
    DaoUtils daoUtils;

    public Uni<EntityA> createA(String uuid, OffsetDateTime expire, EntityA a) {
        return client.withTransaction(conn -> {
            return daoUtils.persist(log, conn, uuid, expire, a);
        });
    }

    public Uni<EntityA> deleteA(String uuid, OffsetDateTime expire, Long id, Long optlock) {
        return client.withTransaction(conn -> daoUtils.find(log, conn, new EntityA(), id)
                .chain(e -> daoUtils.remove(log, conn, uuid, expire, e, optlock)));
    }

    public Uni<EntityA> logicallyDeleteA(String uuid, OffsetDateTime expire, Long id, Long optlock) {
        return client.withTransaction(conn -> daoUtils.find(log, conn, new EntityA(), id)
                .chain(e -> daoUtils.logicallyRemove(log, conn, uuid, expire, e, optlock)));
    }

    public Uni<EntityA> updateA(String uuid, OffsetDateTime expire, Long id, Long optlock, String titlePrefix) {
        return client.withTransaction(conn -> daoUtils.find(log, conn, new EntityA(), id)
                .chain((oldA) -> {
                    EntityA newA = null;
                    if (oldA != null) {
                        newA = oldA.clone();
                        newA.setTitle(titlePrefix + newA.getTitle());
                        newA.setCreatetime(OffsetDateTime.now());
                    }
                    return daoUtils.merge(log, conn, uuid, expire, oldA, newA, optlock);
                }));
    }

    public Uni<EntityA> updateArchivingA(String uuid, OffsetDateTime expire, Long id, Long optlock, String titlePrefix) {
        return client.withTransaction(conn -> daoUtils.find(log, conn, new EntityA(), id)
                .chain((oldA) -> {
                    EntityA newA = null;
                    if (oldA != null) {
                        newA = oldA.clone();
                        newA.setTitle(titlePrefix + newA.getTitle());
                        newA.setCreatetime(OffsetDateTime.now());
                    }
                    return daoUtils.mergeArchiving(log, conn, uuid, expire, oldA, newA, optlock);
                }));
    }

    public Uni<List<ObjectA>> getAllA() {
        return client.withConnection(conn -> daoUtils.getResultList(log, conn, EntityA.FIND_ALL, new EntityA())
                .map(items -> items.stream().map(item -> item.toObjectA()).collect(Collectors.toList())));
    }

    public Uni<ObjectA> getA(Long id) {
        return client.withConnection(conn -> daoUtils.find(log, conn, new EntityA(), id))
                .map(item -> item.toObjectA());

    }

}

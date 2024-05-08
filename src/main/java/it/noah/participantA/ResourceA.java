/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.noah.participantA;

import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import it.noah.common.ABCEventDetails;
import it.noah.common.ABCouple;
import it.noah.common.ABEventDetails;
import it.noah.common.SingleABCEventDetails;
import it.noah.sagacqrs.json.Jsoner;
import it.noah.sagacqrs.participant.ParticipantConfigurator;
import it.noah.sagacqrs.participant.interfaces.IParticipantServer;
import it.noah.participantA.entity.EntityA;
import it.noah.participantA.facade.FacadeA;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.jboss.logging.Logger;

/**
 *
 * @author NATCRI
 */
@Path("/a")
@Startup
@ApplicationScoped
public class ResourceA implements IParticipantServer {

    @Inject
    ParticipantConfigurator configurator;

    @Inject
    Logger log;

    @Inject
    PgPool dbPool;

    @Inject
    Jsoner jsoner;

    @Inject
    FacadeA facade;

    @PostConstruct
    void init() throws Throwable {
        configurator.init(log, dbPool, EntityA.class);
    }

    @Override
    public ParticipantConfigurator getConfigurator() {
        return configurator;
    }

    @Override
    public Uni<Object> execute(@QueryParam(value = "uuid") String uuid, @QueryParam(value = "expire") OffsetDateTime expire,
            @QueryParam(value = "operation") String operation, Object data) {
        switch (operation) {
            case "CREATE_ABC":
                return facade.createA(uuid, expire)
                        .map(a -> {
                            ABCEventDetails details = new ABCEventDetails();
                            details.setEntityAId(a.getId());
                            details.setOptlock(a.getOptlock());
                            return details;
                        });
            case "DELETE_ABC":
                ABCEventDetails details2 = jsoner.getObject(data, ABCEventDetails.class);
                return facade.deleteA(uuid, expire, details2.getEntityAId(), details2.getOptlock()).replaceWith(details2);
            case "LOGICAL_DELETE_ABC":
                ABCEventDetails details3 = jsoner.getObject(data, ABCEventDetails.class);
                return facade.logicallyDeleteA(uuid, expire, details3.getEntityAId(), details3.getOptlock()).replaceWith(details3);
            case "UPDATE_ABC":
                ABCEventDetails details4 = jsoner.getObject(data, ABCEventDetails.class);
                return facade.updateA(uuid, expire, details4.getEntityAId(), details4.getOptlock(), details4.getTitlePrefix()).replaceWith(details4);
            case "UPDATE_ARCHIVING_ABC":
                ABCEventDetails details5 = jsoner.getObject(data, ABCEventDetails.class);
                return facade.updateArchivingA(uuid, expire, details5.getEntityAId(), details5.getOptlock(), details5.getTitlePrefix()).replaceWith(details5);
            case "READ_ALL_AB":
                return facade.getAllA().map(objects -> {
                    List<ABCouple> items = new ArrayList<>();
                    objects.forEach(a -> items.add(new ABCouple(a)));
                    ABEventDetails details = jsoner.getObject(data, ABEventDetails.class);
                    details.setItems(items);
                    return details;
                });
            case "READ_ONE_ABC":
                SingleABCEventDetails details6 = jsoner.getObject(data, SingleABCEventDetails.class);
                return facade.getA(details6.getEntityAId()).map(object -> {
                    details6.setA(object);
                    return details6;
                });
            default:
                return throwNoOperationFound(operation);
        }
    }
}

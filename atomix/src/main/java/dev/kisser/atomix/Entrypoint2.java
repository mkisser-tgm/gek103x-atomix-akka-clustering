package dev.kisser.atomix;

import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.core.list.DistributedList;
import io.atomix.core.lock.DistributedLock;
import io.atomix.core.map.DistributedMap;
import io.atomix.core.profile.Profile;
import io.atomix.protocols.raft.MultiRaftProtocol;
import io.atomix.protocols.raft.ReadConsistency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class Entrypoint2 {
    public static void main(String[] args) {

        Logger log = LoggerFactory.getLogger(Entrypoint2.class);

        AtomixBuilder builder = Atomix.builder();
        Atomix atomix;
        atomix = builder.withMemberId("member2")
                .withAddress("localhost:13371")
                .addProfile(Profile.dataGrid())
                .withMembershipProvider(BootstrapDiscoveryProvider.builder()
                        .withNodes(
                                Node.builder()
                                        .withId("member1")
                                        .withAddress("localhost:13370")
                                        .build(),
                                Node.builder()
                                        .withId("member2")
                                        .withAddress("localhost:13371")
                                        .build())
                        .build())
                .build();

        atomix.start().join();

        DistributedList<Object> list = atomix.listBuilder("kappa-list")
                .withElementType(String.class)
                .build();

        DistributedLock lock = atomix.lockBuilder("kappa-lock")
                        .withProtocol(MultiRaftProtocol.builder()
                                .withReadConsistency(ReadConsistency.LINEARIZABLE)
                        .build()).build();

        log.info("Waiting for benchmark start");
        while (true){
            if(lock.isLocked()){
                break;
            }
        }

        log.info("Starting benchmark...");
        Date start = new Date();
        Date end;
        while (true) {
            if (list.size() == 10000){
                end = new Date();
                break;
            }
        }

        long time = end.getTime() - start.getTime();
        log.info("Benchmark completed: "+ time + "ms");
    }
}

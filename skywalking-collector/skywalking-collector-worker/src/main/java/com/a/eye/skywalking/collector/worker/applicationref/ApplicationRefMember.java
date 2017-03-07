package com.a.eye.skywalking.collector.worker.applicationref;

import akka.actor.ActorRef;
import com.a.eye.skywalking.collector.actor.AbstractMember;
import com.a.eye.skywalking.collector.actor.AbstractSyncMember;
import com.a.eye.skywalking.collector.actor.AbstractSyncMemberProvider;
import com.a.eye.skywalking.collector.actor.selector.RollingSelector;
import com.a.eye.skywalking.collector.worker.applicationref.presistence.DAGNodeRefPersistence;
import com.a.eye.skywalking.trace.TraceSegment;

/**
 * @author pengys5
 */
public class ApplicationRefMember extends AbstractSyncMember {

    public ApplicationRefMember(ActorRef actorRef) throws Throwable {
        super(actorRef);
    }

    @Override
    public void receive(Object message) throws Exception {
        TraceSegment traceSegment = (TraceSegment) message;

        if (traceSegment.getPrimaryRef() != null) {
            String front = traceSegment.getPrimaryRef().getApplicationCode();
            String behind = traceSegment.getApplicationCode();

            DAGNodeRefPersistence.Metric nodeRef = new DAGNodeRefPersistence.Metric(front, behind);
            tell(new DAGNodeRefPersistence.Factory(), RollingSelector.INSTANCE, nodeRef);
        }
    }

    public static class Factory extends AbstractSyncMemberProvider<ApplicationRefMember> {
        public static Factory INSTANCE = new Factory();

        @Override
        public Class memberClass() {
            return ApplicationRefMember.class;
        }
    }
}

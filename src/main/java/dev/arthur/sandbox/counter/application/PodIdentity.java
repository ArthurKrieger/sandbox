package dev.arthur.sandbox.counter.application;

/** Names the pod (ECS task) this instance runs in, so ledger entries show who made each change. */
public interface PodIdentity {

    String name();
}

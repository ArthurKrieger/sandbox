package dev.arthur.sandbox.tracking.domain;

// SIMULATED devices drive a made-up route and are listed publicly; REAL devices report a visitor's actual position
// and are never listed.
public enum DeviceKind {
    SIMULATED,
    REAL
}

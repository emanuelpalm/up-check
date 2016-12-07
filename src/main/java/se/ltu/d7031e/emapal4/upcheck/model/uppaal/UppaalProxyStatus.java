package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

/**
 * Indicates status of some {@link UppaalProxy} instance.
 */
public enum UppaalProxyStatus {
    ENGINE_NOT_CONNECTED,
    ENGINE_INCOMPATIBLE,
    SYSTEM_NOT_FOUND,
    SYSTEM_NOT_PROVIDED,
    SYSTEM_NOT_VALID,
    OK,
}

package se.ltu.dcc.upcheck.model.uppaal;

/**
 * Indicates status of some {@link UppaalProxy} instance.
 */
public enum UppaalProxyStatus {
    ENGINE_ERROR,
    ENGINE_INCOMPATIBLE,
    SYSTEM_NOT_FOUND,
    SYSTEM_NOT_PROVIDED,
    SYSTEM_NOT_VALID,
    OK,
}

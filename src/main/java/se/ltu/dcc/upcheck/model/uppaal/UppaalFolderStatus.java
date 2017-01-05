package se.ltu.dcc.upcheck.model.uppaal;

/**
 * Indicates validity status of some selected UPPAAL installation path.
 */
public enum UppaalFolderStatus {
    NOT_A_DIRECTORY,
    NOT_A_UPPAAL_DIRECTORY,
    NOT_PROVIDED,
    OK,
}

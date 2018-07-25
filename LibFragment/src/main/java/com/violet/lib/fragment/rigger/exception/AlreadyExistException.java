package com.violet.lib.fragment.rigger.exception;

/**
 * Rigger is already exist.
 *
 */

public class AlreadyExistException extends RiggerException {

  public AlreadyExistException(String fragmentTag) {
    super(fragmentTag + " is added into the stack");
  }
}

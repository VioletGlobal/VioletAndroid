package com.violet.lib.fragment.rigger.exception;

/**
 * Rigger is already exist.
 */

public class NotExistException extends RiggerException {

  public NotExistException(String fragmentTag) {
    super(fragmentTag + "is not found in stack");
  }
}

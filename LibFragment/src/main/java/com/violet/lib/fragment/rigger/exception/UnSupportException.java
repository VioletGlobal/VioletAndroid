package com.violet.lib.fragment.rigger.exception;

/**
 * UnSupported operation exception.
 *
 */

public class UnSupportException extends RiggerException {

  public UnSupportException(String operate) {
    super("Unsupported operation [" + operate + "],please check your code");
  }
}

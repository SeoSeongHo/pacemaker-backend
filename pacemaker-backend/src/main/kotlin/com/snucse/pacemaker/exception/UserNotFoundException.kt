package com.snucse.pacemaker.exception

import java.lang.RuntimeException

class UserNotFoundException(msg: String): RuntimeException(msg)
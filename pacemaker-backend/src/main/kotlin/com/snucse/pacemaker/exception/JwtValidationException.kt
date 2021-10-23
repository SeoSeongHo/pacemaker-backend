package com.snucse.pacemaker.exception

import java.lang.RuntimeException

class JwtValidationException(msg: String): RuntimeException(msg)
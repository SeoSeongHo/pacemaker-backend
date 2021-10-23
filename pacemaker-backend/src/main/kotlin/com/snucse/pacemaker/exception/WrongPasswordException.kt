package com.snucse.pacemaker.exception

import java.lang.RuntimeException

class WrongPasswordException(msg: String): RuntimeException(msg)
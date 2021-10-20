package com.snucse.pacemaker.exception

import java.lang.RuntimeException

class DuplicateEmailException(msg: String): RuntimeException(msg)
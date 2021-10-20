package com.snucse.pacemaker.exception

import java.lang.RuntimeException

class DuplicateNicknameException(msg: String): RuntimeException(msg)
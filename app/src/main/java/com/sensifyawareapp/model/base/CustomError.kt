package com.sensifyawareapp.model.base

class CustomError(var errorCode: Int, var error: String) : RuntimeException()
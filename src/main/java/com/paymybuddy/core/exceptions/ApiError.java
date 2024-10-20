package com.paymybuddy.core.exceptions;

import java.time.Instant;

public record ApiError(int i, String message, Instant now) { }

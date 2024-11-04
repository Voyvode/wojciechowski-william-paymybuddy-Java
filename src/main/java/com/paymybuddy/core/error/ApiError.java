package com.paymybuddy.core.error;

import java.time.Instant;

public record ApiError(int i, String message, Instant now) { }

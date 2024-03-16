<?php

use App\Http\Controllers\CVController;
use Illuminate\Support\Facades\Route;

$prefix = env('APP_DEPLOYMENT_NAME', 'default');

Route::prefix($prefix)->group(function () {
    Route::get('/', [CVController::class, 'show']);
    Route::post('/', [CVController::class, 'submitForm']);
    Route::get('/status', [CVController::class, 'updateStatus']);
});

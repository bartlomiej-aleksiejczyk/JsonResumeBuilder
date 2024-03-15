<?php

use App\Http\Controllers\CVController;
use Illuminate\Support\Facades\Route;

Route::get('/cv', [CVController::class, 'show']);
Route::post('/cv', [CVController::class, 'submitForm']);
Route::get('/cv/satus', [CVController::class, 'updateStatus']);

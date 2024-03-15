<?php

namespace App\Http\Controllers;

use App\Jobs\ProcessCV;
use Illuminate\Http\Request;
use Illuminate\View\View;

class CVController extends Controller
{
    public function show(): View
    {
        return view('cv-form');
    }

    public function submitForm(Request $request)
    {
        $data = $request->validate([
            'template_name' => 'required',
            'data' => 'required',
        ]);
        ProcessCV::dispatch($data['template_name'], $data['data']);
        return back()->with('status', 'Job dispatched!');
    }

    public function updateStatus(Request $request)
    {
        // Update job status based on notification received
    }
    public function cancel($jobId)
    {
        // Here, you would need to call the specific logic to cancel the job in the queue.
        // This could be complex as Laravel's default queue system does not directly expose a method for cancelling specific jobs.
        // If using external services or specific job IDs from RabbitMQ, you would implement logic here.
    }
}

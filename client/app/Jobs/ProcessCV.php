<?php

namespace App\Jobs;

use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Bus\Dispatchable;
use Illuminate\Queue\InteractsWithQueue;
use Illuminate\Queue\SerializesModels;

class ProcessCV implements ShouldQueue
{
    use Dispatchable, InteractsWithQueue, Queueable, SerializesModels;

    private string $templateName;
    private object $data;

    public function __construct(string $templateName, $data)
    {
        $this->templateName = $templateName;
        $this->data = $data;
    }

    public function handle(): void
    {
        $data = ['template_name' => $this->templateName, 'data' => $this->data];
    }
}

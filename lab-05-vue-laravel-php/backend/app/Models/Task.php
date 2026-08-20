<?php namespace App\Models; class Task { public function __construct(public int $id,public int $customerId,public string $title,public string $status,public ?string $note){} }

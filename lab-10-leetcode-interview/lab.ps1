param(
  [Parameter(Position=0)][ValidateSet('doctor','start','test','reset')][string]$Command='doctor',
  [Parameter(Position=1)][ValidateSet('easy','intermediate','advanced')][string]$Level='easy',
  [string]$Ticket,
  [string]$Scenario,
  [switch]$Random,
  [ValidateSet('java','python')][string]$Track='java',
  [switch]$Evaluator,
  [switch]$Reference
)
$ErrorActionPreference='Stop'
$Root=$PSScriptRoot
$State=Join-Path $Root '.interview-state.json'
$Pools=@{easy=@('E1','E2','E3');intermediate=@('I1','I4','I5');advanced=@('A1','A2')}
function Select-Ticket {
  $pool=$Pools[$Level]; $selected=if($Ticket){$Ticket}elseif($Scenario){$Scenario}else{$pool[0]}
  $previous=$null;if(Test-Path $State){$saved=Get-Content $State -Raw|ConvertFrom-Json;$previous=$saved.$Level}
  if($Random){$choices=@($pool|Where-Object{$_ -ne $previous});$selected=Get-Random $choices}
  if($selected -notin $pool){throw "Ticket $selected is not valid for $Level"}
  $data=if(Test-Path $State){Get-Content $State -Raw|ConvertFrom-Json}else{[pscustomobject]@{}}
  $data|Add-Member -Force NoteProperty $Level $selected
  $data|ConvertTo-Json|Set-Content -LiteralPath $State
  return $selected
}
Push-Location $Root
try {
  if($Command -eq 'doctor'){docker version --format '{{.Server.Version}}';docker compose version;docker compose config --quiet;Write-Output 'Lab 10 doctor: OK';exit}
  $selected=Select-Ticket
  if($Command -eq 'start'){Write-Output "Selected $Level ticket: $selected";if($Level -eq 'advanced'){docker compose up --build backend frontend}else{Write-Output "Run: .\lab.ps1 test $Level -Ticket $selected"};exit}
  if($Command -eq 'reset'){if($Level -eq 'advanced'){docker compose down --volumes};Write-Output "Reset $selected; selected ticket retained in .interview-state.json";exit}
  if($Level -eq 'easy' -or $Level -eq 'intermediate'){
    if($Track -eq 'python'){
      if($Reference){docker compose --profile tools run --rm python python -m pytest -q reference/test_solutions.py}
      else{$file=@{E1='tests/test_easy_e1_pair_transactions.py';E2='tests/test_easy_e2_transaction_summary.py';E3='tests/test_easy_e3_balanced_events.py';I1='tests/test_intermediate_i1_growth_streak.py';I4='tests/test_intermediate_i4_fraud_clusters.py';I5='tests/test_intermediate_i5_scheduling.py'}[$selected];$mark=if($Evaluator){'public or evaluator or hidden'}else{'public'};docker compose --profile tools run --rm python python -m pytest -q -m $mark $file}
    } else {
      $tag=if($Evaluator){$selected}else{"$selected & public"}
      $mvn=@('mvn','test',"-Dgroups=$tag",'--no-transfer-progress');if($Reference){$mvn+=@('-Preference')}
      docker compose --profile tools run --rm algorithms-java @mvn
    }
  } else {
    $test=if($selected -eq 'A1'){'DuplicateTransferTest'}else{'InvalidStateTransitionTest'}
    $extra=if($Reference){@('-e','LAB_REFERENCE_MODE=true')}else{@()}
    docker compose run --rm @extra backend mvn test "-Dtest=$test" --no-transfer-progress
  }
} finally {Pop-Location}

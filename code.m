%% create signal
clc;
clear;

fs = 48000;
N = 480;
T = N / fs;
t = -N/2/fs : 1 / fs  : N/2/fs-1/fs;
f0 = 10000;
fc = 16000;
B = 12000;
k = B / T;
c = 340;
y = exp(1j*2*pi*(fc*t + 1/2*k*t.^2)).';

win = hann(N);
y = y .* win;
%% reference signal
signal1 = audioread("environment_wo_cardbord.wav");
signal2 = audioread("environment_with_cardbord.wav");

signal1 = signal1(:, 1);
signal2 = signal2(:, 1);


signal1_sync = syncsignal(signal1, y);
signal2_sync = syncsignal(signal2, y);

signal1_sync = signal1_sync(1 : 480);
signal2_sync = signal2_sync(1 : 480);

reference_signal = signal2_sync - signal1_sync;
%% signal preprocessing
signal_list = dir('dataset\*.wav');

for ii = 1 : length(signal_list)
    signal_name = signal_list(ii).name;
    signal{ii} = audioread(signal_name);

    signal_sync{ii} = syncsignal(signal{ii}, reference_signal);

    signal_denoise{ii} = denoisesignal(signal_sync{ii}, reference_signal, fs);

    signal_match_filter{ii} = matchfilter(signal_denoise{ii}, reference_signal);

end


%%Obtain reference scope and ecf during registration stage, and use RA during certification stage
%% get reference range and ECF
signal_1 = audioread("signal_1.wav");
signal_2 = audioread("signal_2.wav");

signal_1_sync = syncsignal(signal_1, reference_signal);
signal_2_sync = syncsignal(signal_2, reference_signal);


signal_1_denoise = denoisesignal(signal_1_sync, reference_signal, fs);
signal_2_denoise = denoisesignal(signal_2_sync, reference_signal, fs);


[near, near_energy] = get_range_and_energy(signal_1_denoise(1 : 480), reference_signal);
[far, far_energy] = get_range_and_energy(signal_2_denoise(1 : 480), reference_signal);





%% RA

for ii = 1 : length(signal_match_filter)
    range_com{ii} = range_compensation(signal_match_filter{ii}, reference_signal, range_reference);
    energy_com{ii} = com_energy(range_com{ii}, near, far, near_energy, far_energy);
end

%% feature exraction

for ii = 1 : length(energy_com)
    fea{ii} = feature_extraction(energy_com(ii));
end






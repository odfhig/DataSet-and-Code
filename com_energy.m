function energy_com = com_energy(signal, near, far, near_energy, far_energy)
    fs = 48000;
    N = 480;
    T = N / fs;
    t = -N/2/fs : 1 / fs  : N/2/fs-1/fs;
    fc = 16000;
    B = 12000;
    k = B / T;
    signal = reshape(signal, 480, []);
    for idx = 1 : n
        a = xcorr(signal(:, idx), ref_signal);
        a = real(a(ceil(length(a)/2):end));
        [pks, loc] = findpeaks(a);
        [~, I] = max(pks);
        distence = loc(I);
        distence = distence / 48000 * 34000 / 2;
        e_ref = (far_energy - near_energy) * (distence - near) / (far - near) + near_energy;

        coef = near_energy / e_ref;
        
        energy_com{i} = signal(:, idx) * coef;
    end
    energy_com = reshape(energy_com, 1, []);
end
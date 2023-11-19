function after_compensation = range_compensation(before_compensation,ref_signal , range_reference)
    n = size(before_compensation, 2);
    fs = 48000;
    N = 480;
    T = N / fs;
    t = -N/2/fs : 1 / fs  : N/2/fs-1/fs;
    fc = 16000;
    B = 12000;
    k = B / T;
    after_compensation = zeros(size(before_compensation));
    for idx = 1 : n
        a = xcorr(before_compensation(:, idx), ref_signal);
        a = real(a(ceil(length(a)/2):end));
        [pks, loc] = findpeaks(a);
        [~, I] = max(pks);
        distence = range_reference - loc(I);
        tau = distence / fs;
        ref = exp(1j*2*pi*(fc*tau-1/2*k*tau^2+tau*k*t)).';
        after_compensation(:, idx) = before_compensation(:, idx) .* ref;
    end
end
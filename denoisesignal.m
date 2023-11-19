function signal_denoise = denoisesignal(signal, ref, fs)
    denoise1 = highpass(signal, 10000, fs);
    
    signal_reshape = reshape(signal, 480, []);

    A = sec_cannel(signal_reshape, ref, 20, 60);

    signal_denoise = reshape(A, 1, []);
    

end